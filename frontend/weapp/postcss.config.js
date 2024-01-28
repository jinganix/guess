module.exports = {
  plugins: {
    autoprefixer: {},
    "postcss-rem-to-responsive-pixel": {
      propList: ["*"],
      rootValue: 32,
      transformUnit: "rpx",
    },
    tailwindcss: {},
  },
};
