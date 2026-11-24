import * as path from "path";
import { checkFile, checkWxml } from "../scripts/check-wxml-bind";

const AVATAR_WXML = `<wxs src="../../wxs/clsx.wxs" module="clsx"/>
<view class="{{rootClass}}">
  <image
    class="{{clsx(['bg-yellow-0', !user.avatar], iconClass)}}"
    src="{{user.avatar || '../../assets/images/anonymous.png'}}"
    bind:tap="tapAvatar"
  />
</view>`;
const AVATAR_FIELDS = new Set(["userId", "rootClass", "iconClass", "user"]);

describe("checkWxml", () => {
  it("accepts the real avatar template", () => {
    expect(checkWxml(AVATAR_WXML, AVATAR_FIELDS)).toEqual([]);
  });

  it("reports an unknown binding", () => {
    expect(checkWxml(`<view>{{typoField}}</view>`, AVATAR_FIELDS)).toEqual(["typoField"]);
  });

  it("does not flag string-literal fallbacks or wxs module names", () => {
    expect(checkWxml(AVATAR_WXML, AVATAR_FIELDS)).not.toContain("clsx");
    expect(checkWxml(AVATAR_WXML, AVATAR_FIELDS)).not.toContain(
      "../../assets/images/anonymous.png",
    );
  });

  it("whitelists wx:for loop locals", () => {
    const wxml = `<view wx:for="{{cacheKeys}}"><comment cache-key="{{item}}"/></view>`;
    expect(checkWxml(wxml, new Set(["cacheKeys"]))).toEqual([]);
  });

  it("resolves the real avatar files with zero errors", () => {
    const dir = path.join(__dirname, "..", "src", "components", "avatar");
    expect(checkFile(dir)).toEqual([]);
  });

  it("resolves reused scripts and inline data with zero errors", () => {
    const root = path.join(__dirname, "..", "src");
    expect(checkFile(path.join(root, "pages", "user", "moments"))).toEqual([]);
    expect(checkFile(path.join(root, "components", "preview-page"))).toEqual([]);
  });
});
