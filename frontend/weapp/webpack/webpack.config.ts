import * as webpack from "webpack";
import { ModuleOptions, RuleSetUseItem } from "webpack";
import * as path from "path";
import { UnifiedWebpackPluginV5 } from "weapp-tailwindcss/webpack";
import TsconfigPathsPlugin from "tsconfig-paths-webpack-plugin";
import TerserPlugin = require("terser-webpack-plugin");
import MiniCssExtractPlugin = require("mini-css-extract-plugin");
import CssMinimizerPlugin = require("css-minimizer-webpack-plugin");
import CopyPlugin = require("copy-webpack-plugin");
import ESLintWebpackPlugin = require("eslint-webpack-plugin");
import { WebpackConfig } from "./types";
import { moduleLoader, PathMappers, requireFile } from "./utils";
import { EntryResolver } from "./entry.resolver";
import { Environment } from "./env/env";

const ROOT_DIR = path.resolve(".");
const SRC_DIR = path.resolve("src");
const VANT_DIR = path.resolve("node_modules/@vant");

const config: WebpackConfig = {
  components: {
    "/components": "src/components",
    "/vant": "node_modules/@vant/weapp/lib",
  },
  entryMappers: new PathMappers([
    {
      mapping: (entry) => entry.replace(/node_modules[\\/]@vant[\\/]weapp[\\/]lib/, "../.tmp/vant"),
      supports: (entry) => entry.startsWith("node_modules/@vant"),
    },
    {
      mapping: (entry) => entry.replace(/src\//, ""),
      supports: (entry) => entry.startsWith("src/"),
    },
  ]),
  environment: {} as Environment,
  exclude: (path: string) => /.*node_modules[\\/]@vant[\\/]weapp[\\/].*\.d\.ts$/.test(path),
  fileMappers: new PathMappers([
    {
      mapping: (entry) => entry.replace(/node_modules[\\/]@vant[\\/]weapp[\\/]lib/, "vant"),
      supports: (entry) => entry.startsWith("node_modules/@vant"),
    },
    {
      mapping: (entry) => entry.replace(/src\//, ""),
      supports: (entry) => entry.startsWith("src/"),
    },
  ]),
};

const fileLoader = (context?: string): RuleSetUseItem => {
  return {
    loader: "file-loader",
    options: {
      context: context ?? ROOT_DIR,
      esModule: false,
      name: `[path][name].[ext]`,
      outputPath: (url: string) => config.fileMappers.mapping(url),
      useRelativePath: false,
    },
  };
};

const dumpLoader = (context?: string): RuleSetUseItem => {
  return {
    loader: path.resolve("webpack/dump-loader/index.ts"),
    options: {
      context: context ?? ROOT_DIR,
      mapOutput: (url: string) => config.fileMappers.mapping(url),
      name: `[path][name].[ext]`,
    },
  };
};

const wxsLoader = (options?: { minify?: boolean }): RuleSetUseItem => {
  return {
    loader: path.resolve("webpack/wxs-loader/index.ts"),
    options: options,
  };
};

const wxjsonLoader = (options?: { minify?: boolean }): RuleSetUseItem => {
  return {
    loader: path.resolve("webpack/wxjson-loader/index.ts"),
    options: options,
  };
};

const wxssLoader = (options?: { minify?: boolean; rpx?: boolean }): RuleSetUseItem => {
  return {
    loader: path.resolve("webpack/wxss-loader/index.ts"),
    options: options,
  };
};

const wxmlLoader = (options?: {
  enforceRelativePath?: boolean;
  minimize?: boolean;
}): RuleSetUseItem => {
  return {
    loader: path.resolve("webpack/wxml-loader/index.ts"),
    options: options,
  };
};

export default (env: string | { dev: boolean }): webpack.Configuration => {
  const isDev: boolean = typeof env === "string" ? env !== "prod" : env.dev;
  const { environment }: { environment: Environment } = requireFile(
    path.resolve(`./webpack/env/${isDev ? "dev" : "prod"}.env.ts`),
  );
  config.environment = environment;

  const entries = new EntryResolver(ROOT_DIR, SRC_DIR, config).resolve();
  moduleLoader.addEntries(entries);
  return {
    devtool: isDev ? "source-map" : false,
    entry: entries,
    module: {
      rules: [
        {
          include: [VANT_DIR],
          test: /\.js$/,
          use: [dumpLoader()],
        },
        {
          include: [SRC_DIR, path.resolve("build")],
          test: /\.(ts|tsx)$/,
          use: ["ts-loader", "source-map-loader"],
        },
        {
          include: [SRC_DIR, VANT_DIR],
          test: /\.wxs$/,
          use: [fileLoader(), wxsLoader({ minify: !isDev })],
        },
        {
          include: [VANT_DIR],
          test: /\.wxss$/,
          use: [fileLoader(), wxssLoader({ rpx: false })],
        },
        {
          include: [SRC_DIR, VANT_DIR],
          test: /\.json$/,
          use: [dumpLoader(), wxjsonLoader({ minify: !isDev })],
        },
        {
          include: [SRC_DIR, VANT_DIR],
          test: /\.wxml$/,
          use: [
            fileLoader(),
            wxmlLoader({
              enforceRelativePath: false,
              minimize: !isDev,
            }),
          ],
        },
        {
          include: [SRC_DIR],
          test: /\.less$/,
          use: [
            MiniCssExtractPlugin.loader,
            wxssLoader({ rpx: true }),
            "css-loader",
            "less-loader",
          ],
        },
        {
          include: /src/,
          test: /\.(scss)$/,
          use: [
            {
              loader: "file-loader",
              options: {
                context: path.resolve("src"),
                name: "[path][name].wxss",
                useRelativePath: true,
              },
            },
            {
              loader: "postcss-loader",
            },
            {
              loader: "sass-loader",
              options: {
                sassOptions: {
                  includePaths: [path.resolve("src", "styles"), path.resolve("src")],
                },
              },
            },
          ],
        },
        {
          include: [SRC_DIR],
          test: /\.(png|jpg|gif$)$/,
          use: [fileLoader()],
        },
      ],
    } as ModuleOptions,
    optimization: {
      minimize: true,
      minimizer: [
        !isDev &&
          new TerserPlugin({
            extractComments: false,
            test: /\.js(\?.*)?$/i,
          }),
        !isDev &&
          new CssMinimizerPlugin({
            test: /\.(css|wxss)$/g,
          }),
      ].filter(Boolean),
      runtimeChunk: { name: "runtime" },
      splitChunks: {
        cacheGroups: {
          components: {
            chunks: "all",
            name: "components",
            test: /[\\/]src[\\/](pages|components)[\\/].*script\.(js|jsx|ts|tsx)$/,
          },
          generated: {
            chunks: "all",
            enforce: true,
            name: "generated",
            test: /[\\/]build[\\/]generated[\\/].*\.(js|jsx|ts|tsx)$/,
          },
          helpers: {
            chunks: "all",
            enforce: true,
            name: "helpers",
            test: /[\\/]src[\\/]helpers[\\/].*\.(js|jsx|ts|tsx)$/,
          },
          modules: {
            chunks: "all",
            enforce: true,
            name: "modules",
            test: /[\\/]src[\\/]modules[\\/].*\.(js|jsx|ts|tsx)$/,
          },
          vendors: {
            chunks: "all",
            name: "vendors",
            test: /[\\/]node_modules[\\/](?!@vant)/,
          },
        },
      },
    },
    output: {
      filename: "[name].js",
      globalObject: "global",
      path: path.resolve("dist"),
      publicPath: "/",
    },
    plugins: [
      new CopyPlugin({
        patterns: [
          {
            from: "src/assets/images/*",
            to: "assets/images/[name][ext]",
          },
        ],
      }),
      new ESLintWebpackPlugin({
        emitError: true,
        emitWarning: true,
        exclude: ["build", "node_modules"],
        extensions: ["ts", "tsx"],
        failOnError: true,
        overrideConfigFile: ".eslintrc.js",
      }),
      new MiniCssExtractPlugin({ filename: `[name].wxss` }),
      new webpack.DefinePlugin({
        "process.env": JSON.stringify(environment),
      }),
      new webpack.BannerPlugin({
        banner:
          `try {` +
          `require('./runtime');` +
          `require('./vendors');` +
          `require('./generated');` +
          `require('./helpers');` +
          `require('./modules');` +
          `require('./components');` +
          `} catch (e) {}`,
        include: "app.js",
        raw: true,
      }),
      new UnifiedWebpackPluginV5(),
    ],
    resolve: {
      extensions: [".ts", ".js", ".wxs"],
      modules: [SRC_DIR, "node_modules"],
      plugins: [new TsconfigPathsPlugin()],
      preferRelative: true,
    },
    stats: "minimal",
    watchOptions: {
      aggregateTimeout: 300,
      ignored: /dist|node_modules/,
      poll: 1000,
    },
  } as webpack.Configuration;
};
