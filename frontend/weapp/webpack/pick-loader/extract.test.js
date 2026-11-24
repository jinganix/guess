const { extractPicks } = require("./extract");

describe("extractPicks", () => {
  it("extracts identifiers and member paths", () => {
    expect(extractPicks(`<view>{{loading}}</view>`)).toEqual(["loading"]);
    expect(extractPicks(`<view>{{user.avatar}}</view>`)).toEqual(["user.avatar"]);
    expect(extractPicks(`<view>{{moment.options.length}}</view>`)).toEqual([
      "moment.options.length",
    ]);
  });

  it("handles fallback, negation and ternary (avatar case)", () => {
    const wxml = `<wxs src="../../wxs/clsx.wxs" module="clsx"/>
<view class="{{rootClass}}">
  <image
    class="{{clsx(['bg-yellow-0', !user.avatar], iconClass)}}"
    src="{{user.avatar || '../../assets/images/anonymous.png'}}"
  />
</view>`;
    expect(extractPicks(wxml)).toEqual(["iconClass", "rootClass", "user.avatar"]);
  });

  it("excludes the wxs module callee but keeps other arguments", () => {
    expect(
      extractPicks(`<view class="{{clsx(['a', item.followed], 'b')}}">{{item.followed}}</view>`),
    ).toEqual(["item.followed"]);
  });

  it("excludes wxs module member calls while keeping their arguments", () => {
    const wxml = `<wxs module="fmt" src="./fmt.wxs"/><view>{{fmt.date(ts)}}</view>`;
    expect(extractPicks(wxml)).toEqual(["ts"]);
  });

  it("collects computed non-literal keys in addition to the base path", () => {
    expect(extractPicks(`<input value="{{options[item]}}"/>`)).toEqual(["item", "options"]);
    expect(extractPicks(`<view>{{options[item.key]}}</view>`)).toEqual([
      "item.key",
      "options",
    ]);
  });

  it("ignores numeric computed keys", () => {
    expect(extractPicks(`<view>{{options[0]}}</view>`)).toEqual(["options"]);
  });

  it("maps string-literal computed keys to dotted paths", () => {
    expect(extractPicks(`<view>{{options['nick']}}</view>`)).toEqual(["options.nick"]);
  });

  it("drops trailing paths after a computed object", () => {
    // real case: {{options[item].length || 0}} -> options + item
    expect(extractPicks(`<view>{{options[item].length || 0}}</view>`)).toEqual([
      "item",
      "options",
    ]);
  });

  it("excludes JS globals used as identifiers", () => {
    expect(extractPicks(`<view>{{user.nick === undefined}}</view>`)).toEqual(["user.nick"]);
    expect(extractPicks(`<view>{{count === NaN}}</view>`)).toEqual(["count"]);
  });

  it("supports multiline interpolations", () => {
    expect(extractPicks("<view>{{\n  loading\n}}</view>")).toEqual(["loading"]);
  });

  it("ignores interpolations inside html comments", () => {
    expect(extractPicks(`<view><!-- {{loading}} --></view>`)).toEqual([]);
  });

  it("dedupes and sorts results", () => {
    expect(extractPicks(`<view>{{b}}{{a}}{{b}}{{a.c}}</view>`)).toEqual(["a", "a.c", "b"]);
  });

  it("throws on unparsable expressions instead of returning partial results", () => {
    expect(() => extractPicks(`<view>{{a %% b}}</view>`)).toThrow(/a %% b/);
  });

  it("keeps wx:for loop variables (they may be script observables)", () => {
    // e.g. icon-follow binds its own observable `item`; excluding
    // for-item/for-index names would silently break such components.
    expect(extractPicks(`<view wx:for="{{cacheKeys}}">{{item}}{{index}}</view>`)).toEqual([
      "cacheKeys",
      "index",
      "item",
    ]);
  });
});
