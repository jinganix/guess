/* eslint-disable max-len */
import AuthorizeOption = WechatMiniprogram.AuthorizeOption;
import BaseEvent = WechatMiniprogram.BaseEvent;
import BoundingClientRectCallback = WechatMiniprogram.BoundingClientRectCallback;
import BoundingClientRectCallbackResult = WechatMiniprogram.BoundingClientRectCallbackResult;
import Canvas = WechatMiniprogram.Canvas;
import CanvasContext = WechatMiniprogram.CanvasContext;
import CanvasToTempFilePathSuccessCallbackResult = WechatMiniprogram.CanvasToTempFilePathSuccessCallbackResult;
import ChooseVideoSuccessCallbackResult = WechatMiniprogram.ChooseVideoSuccessCallbackResult;
import CompData = WechatMiniprogram.Component.Data;
import CompDataOption = WechatMiniprogram.Component.DataOption;
import CompInstanceMethods = WechatMiniprogram.Component.InstanceMethods;
import CompInstanceProperties = WechatMiniprogram.Component.InstanceProperties;
import CompLifetimes = WechatMiniprogram.Component.Lifetimes;
import CompOptions = WechatMiniprogram.Component.Options;
import CompOtherOption = WechatMiniprogram.Component.OtherOption;
import ContextCallbackResult = WechatMiniprogram.ContextCallbackResult;
import CustomEvent = WechatMiniprogram.CustomEvent;
import CustomOption = WechatMiniprogram.Page.CustomOption;
import DownloadFileOption = WechatMiniprogram.DownloadFileOption;
import DownloadFileSuccessCallbackResult = WechatMiniprogram.DownloadFileSuccessCallbackResult;
import FullProperty = WechatMiniprogram.Component.FullProperty;
import GeneralCallbackResult = WechatMiniprogram.GeneralCallbackResult;
import GetFileInfoOption = WechatMiniprogram.GetFileInfoOption;
import GetFileInfoSuccessCallbackResult = WechatMiniprogram.GetFileInfoSuccessCallbackResult;
import GetImageInfoOption = WechatMiniprogram.GetImageInfoOption;
import GetImageInfoSuccessCallbackResult = WechatMiniprogram.GetImageInfoSuccessCallbackResult;
import GetSettingOption = WechatMiniprogram.GetSettingOption;
import GetSettingSuccessCallbackResult = WechatMiniprogram.GetSettingSuccessCallbackResult;
import GetUserInfoSuccessCallbackResult = WechatMiniprogram.GetUserInfoSuccessCallbackResult;
import GetUserProfileOption = WechatMiniprogram.GetUserProfileOption;
import GetUserProfileSuccessCallbackResult = WechatMiniprogram.GetUserProfileSuccessCallbackResult;
import HideLoadingOption = WechatMiniprogram.HideLoadingOption;
import HideTabBarOption = WechatMiniprogram.HideTabBarOption;
import HideToastOption = WechatMiniprogram.HideToastOption;
import IAnyObject = WechatMiniprogram.IAnyObject;
import ICustomShareContent = WechatMiniprogram.Page.ICustomShareContent;
import ICustomTimelineContent = WechatMiniprogram.Page.ICustomTimelineContent;
import IShareAppMessageOption = WechatMiniprogram.Page.IShareAppMessageOption;
import Image = WechatMiniprogram.Image;
import Input = WechatMiniprogram.Input;
import InstanceMethods = WechatMiniprogram.Page.InstanceMethods;
import InterstitialAd = WechatMiniprogram.InterstitialAd;
import LaunchShowOption = WechatMiniprogram.App.LaunchShowOption;
import LoginOption = WechatMiniprogram.LoginOption;
import LoginSuccessCallbackResult = WechatMiniprogram.LoginSuccessCallbackResult;
import MediaFile = WechatMiniprogram.MediaFile;
import MethodOption = WechatMiniprogram.Component.MethodOption;
import NavigateBackOption = WechatMiniprogram.NavigateBackOption;
import NavigateToOption = WechatMiniprogram.NavigateToOption;
import NavigateToSuccessCallbackResult = WechatMiniprogram.NavigateToSuccessCallbackResult;
import NodeCallbackResult = WechatMiniprogram.NodeCallbackResult;
import OpenSettingOption = WechatMiniprogram.OpenSettingOption;
import OpenSettingSuccessCallbackResult = WechatMiniprogram.OpenSettingSuccessCallbackResult;
import PageData = WechatMiniprogram.Page.Data;
import PageDataOption = WechatMiniprogram.Page.DataOption;
import PageInstanceMethods = WechatMiniprogram.Page.InstanceMethods;
import PageInstanceProperties = WechatMiniprogram.Page.InstanceProperties;
import PageLifetimes = WechatMiniprogram.Page.ILifetime;
import PageOptions = WechatMiniprogram.Page.Options;
import PreviewImageOption = WechatMiniprogram.PreviewImageOption;
import PromisifySuccessResult = WechatMiniprogram.PromisifySuccessResult;
import PropertyOption = WechatMiniprogram.Component.PropertyOption;
import PropertyType = WechatMiniprogram.Component.PropertyType;
import ReLaunchOption = WechatMiniprogram.ReLaunchOption;
import RedirectToOption = WechatMiniprogram.RedirectToOption;
import RemoveStorageOption = WechatMiniprogram.RemoveStorageOption;
import RenderingContext = WechatMiniprogram.RenderingContext;
import RequestOption = WechatMiniprogram.RequestOption;
import RequestPaymentOption = WechatMiniprogram.RequestPaymentOption;
import RequestSubscribeMessageOption = WechatMiniprogram.RequestSubscribeMessageOption;
import RequestSuccessCallbackResult = WechatMiniprogram.RequestSuccessCallbackResult;
import RewardedVideoAd = WechatMiniprogram.RewardedVideoAd;
import SaveImageToPhotosAlbumOption = WechatMiniprogram.SaveImageToPhotosAlbumOption;
import SaveVideoToPhotosAlbumOption = WechatMiniprogram.SaveVideoToPhotosAlbumOption;
import SetClipboardDataOption = WechatMiniprogram.SetClipboardDataOption;
import SetNavigationBarTitleOption = WechatMiniprogram.SetNavigationBarTitleOption;
import SetStorageOption = WechatMiniprogram.SetStorageOption;
import ShowLoadingOption = WechatMiniprogram.ShowLoadingOption;
import ShowModalOption = WechatMiniprogram.ShowModalOption;
import ShowModalSuccessCallbackResult = WechatMiniprogram.ShowModalSuccessCallbackResult;
import ShowTabBarOption = WechatMiniprogram.ShowTabBarOption;
import ShowToastOption = WechatMiniprogram.ShowToastOption;
import StopPullDownRefreshOption = WechatMiniprogram.StopPullDownRefreshOption;
import TouchEvent = WechatMiniprogram.TouchEvent;
import TrivialInstance = WechatMiniprogram.Component.TrivialInstance;
import UploadFileOption = WechatMiniprogram.UploadFileOption;
import UploadFileSuccessCallbackResult = WechatMiniprogram.UploadFileSuccessCallbackResult;
import VideoContext = WechatMiniprogram.VideoContext;
/* eslint-enable max-len */

export type {
  AuthorizeOption,
  BaseEvent,
  BoundingClientRectCallback,
  BoundingClientRectCallbackResult,
  Canvas,
  CanvasContext,
  CanvasToTempFilePathSuccessCallbackResult,
  ChooseVideoSuccessCallbackResult,
  CompData,
  CompDataOption,
  CompInstanceMethods,
  CompInstanceProperties,
  CompLifetimes,
  CompOptions,
  CompOtherOption,
  ContextCallbackResult,
  CustomEvent,
  CustomOption,
  DownloadFileOption,
  DownloadFileSuccessCallbackResult,
  FullProperty,
  GeneralCallbackResult,
  GetFileInfoOption,
  GetFileInfoSuccessCallbackResult,
  GetImageInfoOption,
  GetImageInfoSuccessCallbackResult,
  GetSettingOption,
  GetSettingSuccessCallbackResult,
  GetUserInfoSuccessCallbackResult,
  GetUserProfileOption,
  GetUserProfileSuccessCallbackResult,
  HideLoadingOption,
  HideTabBarOption,
  HideToastOption,
  IAnyObject,
  ICustomShareContent,
  ICustomTimelineContent,
  IShareAppMessageOption,
  Image,
  Input,
  InstanceMethods,
  InterstitialAd,
  LaunchShowOption,
  LoginOption,
  LoginSuccessCallbackResult,
  MediaFile,
  MethodOption,
  NavigateBackOption,
  NavigateToOption,
  NavigateToSuccessCallbackResult,
  NodeCallbackResult,
  OpenSettingOption,
  OpenSettingSuccessCallbackResult,
  PageData,
  PageDataOption,
  PageInstanceMethods,
  PageInstanceProperties,
  PageLifetimes,
  PageOptions,
  PreviewImageOption,
  PromisifySuccessResult,
  PropertyOption,
  PropertyType,
  ReLaunchOption,
  RedirectToOption,
  RemoveStorageOption,
  RenderingContext,
  RequestOption,
  RequestPaymentOption,
  RequestSubscribeMessageOption,
  RequestSuccessCallbackResult,
  RewardedVideoAd,
  SaveImageToPhotosAlbumOption,
  SaveVideoToPhotosAlbumOption,
  SetClipboardDataOption,
  SetNavigationBarTitleOption,
  SetStorageOption,
  ShowLoadingOption,
  ShowModalOption,
  ShowModalSuccessCallbackResult,
  ShowTabBarOption,
  ShowToastOption,
  StopPullDownRefreshOption,
  TouchEvent,
  TrivialInstance,
  UploadFileOption,
  UploadFileSuccessCallbackResult,
  VideoContext,
};

export interface TriggeredEvent<Detail extends IAnyObject | number | string = IAnyObject>
  extends BaseEvent<IAnyObject, IAnyObject, IAnyObject> {
  detail: Detail;
}

export interface TappedEvent<CurrentTargetDataset extends IAnyObject = IAnyObject>
  extends BaseEvent<IAnyObject, CurrentTargetDataset, IAnyObject> {}
