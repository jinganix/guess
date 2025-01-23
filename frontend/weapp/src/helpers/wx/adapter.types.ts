import { ComponentScript } from "@helpers/wx/component.script";
import {
  CompData,
  CompDataOption,
  CompInstanceMethods,
  CompInstanceProperties,
  CompLifetimes,
  CompOtherOption,
  PageData,
  PageDataOption,
  PageInstanceMethods,
  PageInstanceProperties,
  PageLifetimes,
  PropertyOption,
} from "@helpers/wx/wx.types";

interface ScriptOption<T extends ComponentScript> {
  script: T;
}

interface Properties {
  properties: PropertyOption;
}

export interface ScriptedPage<TScript = ComponentScript>
  extends PageData<PageDataOption>,
    PageLifetimes,
    PageInstanceProperties,
    PageInstanceMethods<PageDataOption>,
    ScriptOption<TScript> {}

// eslint-disable-next-line @typescript-eslint/no-unsafe-declaration-merging,@typescript-eslint/no-unused-vars
export class ScriptedPage<TScript extends ComponentScript> {}

export interface ScriptedComponent<TScript = ComponentScript>
  extends CompData<CompDataOption>,
    CompLifetimes,
    CompOtherOption,
    CompInstanceProperties,
    CompInstanceMethods<CompDataOption>,
    Properties,
    ScriptOption<TScript> {}

// eslint-disable-next-line @typescript-eslint/no-unsafe-declaration-merging,@typescript-eslint/no-unused-vars
export class ScriptedComponent<TScript extends ComponentScript> {}
