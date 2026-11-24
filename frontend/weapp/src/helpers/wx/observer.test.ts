import { cast } from "@helpers/utils/utils";
import { makeObservable, observable } from "mobx";

// component.script pulls @modules/container, which cycles back through
// component scripts under jest; the container is irrelevant to these tests.
jest.mock("@modules/container", () => ({
  components: { remove: jest.fn(), set: jest.fn() },
}));

import { ScriptedComponent } from "./adapter.types";
import { ComponentScript } from "./component.script";
import { Observer } from "./observer";

class Box {
  user = { avatar: "a", nick: "n" };
  loading = false;

  constructor() {
    makeObservable(this, { loading: observable, user: observable });
  }
}

describe("Observer without picks (full binding)", () => {
  it("observes nested objects wholly when picks are absent", () => {
    const box = new Box();
    const calls: Array<[string, unknown]> = [];
    const observer = new Observer((path, value) => calls.push([path, value]));
    observer.observe(box);
    box.user = { avatar: "b", nick: "n" };
    expect(calls).toEqual([["user", { avatar: "b", nick: "n" }]]);
    box.user.avatar = "c";
    expect(calls[1]).toEqual(["user.avatar", "c"]);
    observer.dispose();
  });

  it("keeps honoring explicit picks", () => {
    const box = new Box();
    const calls: Array<[string, unknown]> = [];
    const observer = new Observer((path, value) => calls.push([path, value]), ["user.avatar"]);
    observer.observe(box);
    box.user = { avatar: "b", nick: "n" };
    expect(calls).toEqual([["user", { avatar: "b" }]]);
    observer.dispose();
  });
});

describe("ComponentScript without picks (full binding)", () => {
  class FullScript extends ComponentScript {
    @observable accessor name = "";

    constructor() {
      super(cast(null) as ScriptedComponent);
    }

    classId(): string {
      return "full-script";
    }
  }

  it("exposes all observables via defaultData", () => {
    expect(new FullScript().defaultData()).toEqual({ name: "" });
  });
});
