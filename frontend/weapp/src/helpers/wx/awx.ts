/*
 * Copyright (c) 2020 jinganix@gmail.com, All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * https://github.com/jinganix/guess
 */

import {
  AuthorizeOption,
  PageDataOption,
  DownloadFileOption,
  DownloadFileSuccessCallbackResult,
  GeneralCallbackResult,
  GetFileInfoOption,
  GetFileInfoSuccessCallbackResult,
  GetImageInfoOption,
  GetImageInfoSuccessCallbackResult,
  GetSettingOption,
  GetSettingSuccessCallbackResult,
  GetUserProfileOption,
  GetUserProfileSuccessCallbackResult,
  HideLoadingOption,
  HideTabBarOption,
  HideToastOption,
  IAnyObject,
  InstanceMethods,
  LoginOption,
  LoginSuccessCallbackResult,
  NavigateBackOption,
  NavigateToOption,
  NavigateToSuccessCallbackResult,
  OpenSettingOption,
  OpenSettingSuccessCallbackResult,
  PreviewImageOption,
  ReLaunchOption,
  RedirectToOption,
  RemoveStorageOption,
  RequestOption,
  RequestPaymentOption,
  RequestSubscribeMessageOption,
  RequestSuccessCallbackResult,
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
  UploadFileOption,
  UploadFileSuccessCallbackResult,
} from "@helpers/wx/wx.types";

export type RequestResult = RequestSuccessCallbackResult<IAnyObject>;

export interface RequestOptions {
  data: string;
  headers: Record<string, string>;
  method: string;
  url: string;
}

export type Method = "OPTIONS" | "GET" | "HEAD" | "POST" | "PUT" | "DELETE" | "TRACE" | "CONNECT";

class Awx {
  async toPromise<T, R>(fn: (opt: T) => void, option?: T): Promise<R> {
    return new Promise((resolve, reject) => {
      fn({
        fail: (res: GeneralCallbackResult) => reject(res),
        success: (res: R) => resolve(res),
        ...option,
      } as unknown as T);
    });
  }

  showLoading(option: ShowLoadingOption): Promise<GeneralCallbackResult> {
    return this.toPromise<ShowLoadingOption, GeneralCallbackResult>(
      (opt) => void wx.showLoading(opt),
      option,
    );
  }

  request(option: RequestOption): Promise<RequestSuccessCallbackResult> {
    return this.toPromise<RequestOption, RequestSuccessCallbackResult>(
      (opt) =>
        void wx.request({
          dataType: "json",
          responseType: "text",
          timeout: 5000,
          ...opt,
        }),
      option,
    );
  }

  stopPullDownRefresh(option?: ShowLoadingOption): Promise<GeneralCallbackResult> {
    return wx.stopPullDownRefresh(option);
  }

  hideLoading(option?: HideLoadingOption): Promise<GeneralCallbackResult> {
    return wx.hideLoading(option);
  }

  showToast(option: ShowToastOption): Promise<GeneralCallbackResult> {
    return wx.showToast(option);
  }

  setStorage(option: SetStorageOption): Promise<GeneralCallbackResult> {
    return wx.setStorage(option);
  }

  removeStorage(option: RemoveStorageOption): Promise<GeneralCallbackResult> {
    return wx.removeStorage(option);
  }

  setData<TData extends PageDataOption>(
    comp: InstanceMethods<TData>,
    data: Partial<TData>,
  ): Promise<void> {
    return new Promise<void>((resolve) => {
      comp.setData(data, () => resolve());
    });
  }

  hideToast(option?: HideToastOption): Promise<GeneralCallbackResult> {
    return this.toPromise<HideToastOption, GeneralCallbackResult>(
      (opt) => void wx.hideToast(opt),
      option,
    );
  }

  getSetting(option: GetSettingOption): Promise<GetSettingSuccessCallbackResult> {
    return wx.getSetting(option);
  }

  openSetting(option: OpenSettingOption): Promise<OpenSettingSuccessCallbackResult> {
    return wx.openSetting(option);
  }

  authorize(option: AuthorizeOption): Promise<GeneralCallbackResult> {
    return wx.authorize(option);
  }

  login(option: LoginOption): Promise<LoginSuccessCallbackResult> {
    return wx.login(option);
  }

  navigateBack(option?: NavigateBackOption): Promise<GeneralCallbackResult> {
    return wx.navigateBack(option);
  }

  navigateTo(option: NavigateToOption): Promise<NavigateToSuccessCallbackResult> {
    return wx.navigateTo(option);
  }

  redirectTo(option: RedirectToOption): Promise<GeneralCallbackResult> {
    return wx.redirectTo(option);
  }

  relaunch(url: string): Promise<GeneralCallbackResult> {
    return this.toPromise<ReLaunchOption, GeneralCallbackResult>(
      (option) => void wx.reLaunch(option),
      { url } as ReLaunchOption,
    );
  }

  uploadFile(option: UploadFileOption): Promise<UploadFileSuccessCallbackResult> {
    return this.toPromise<UploadFileOption, UploadFileSuccessCallbackResult>(
      (opt) => void wx.uploadFile(opt),
      option,
    );
  }

  requestSubscribeMessage(option: RequestSubscribeMessageOption): Promise<GeneralCallbackResult> {
    return this.toPromise<RequestSubscribeMessageOption, GeneralCallbackResult>(
      (opt) => void wx.requestSubscribeMessage(opt),
      option,
    );
  }

  getUserProfile(): Promise<GetUserProfileSuccessCallbackResult> {
    return this.toPromise<GetUserProfileOption, GetUserProfileSuccessCallbackResult>(
      (opt) => void wx.getUserProfile(opt),
      {
        desc: "仅获取头像、昵称等公开数据",
      },
    );
  }

  downloadFile(option: DownloadFileOption): Promise<DownloadFileSuccessCallbackResult> {
    return this.toPromise<DownloadFileOption, DownloadFileSuccessCallbackResult>(
      (opt) => void wx.downloadFile(opt),
      option,
    );
  }

  saveImageToPhotosAlbum(option: SaveImageToPhotosAlbumOption): Promise<GeneralCallbackResult> {
    return this.toPromise<SaveImageToPhotosAlbumOption, GeneralCallbackResult>(
      (opt) => void wx.saveImageToPhotosAlbum(opt),
      option,
    );
  }

  saveVideoToPhotosAlbum(option: SaveVideoToPhotosAlbumOption): Promise<GeneralCallbackResult> {
    return this.toPromise<SaveVideoToPhotosAlbumOption, GeneralCallbackResult>(
      (opt) => void wx.saveVideoToPhotosAlbum(opt),
      option,
    );
  }

  showModal(option: ShowModalOption): Promise<ShowModalSuccessCallbackResult> {
    return this.toPromise<ShowModalOption, ShowModalSuccessCallbackResult>(
      (opt) => void wx.showModal(opt),
      option,
    );
  }

  requestPayment(option: RequestPaymentOption): Promise<GeneralCallbackResult> {
    return this.toPromise<RequestPaymentOption, GeneralCallbackResult>(
      (opt) => void wx.requestPayment(opt),
      option,
    );
  }

  setNavigationBarTitle(option: SetNavigationBarTitleOption): Promise<GeneralCallbackResult> {
    return this.toPromise<SetNavigationBarTitleOption, GeneralCallbackResult>(
      (opt) => void wx.setNavigationBarTitle(opt),
      option,
    );
  }

  previewImage(option: PreviewImageOption): Promise<GeneralCallbackResult> {
    return this.toPromise<PreviewImageOption, GeneralCallbackResult>(
      (opt) => void wx.previewImage(opt),
      option,
    );
  }

  setClipboardData(option: SetClipboardDataOption): Promise<GeneralCallbackResult> {
    return this.toPromise<SetClipboardDataOption, GeneralCallbackResult>(
      (opt) => void wx.setClipboardData(opt),
      option,
    );
  }

  showTabBar(option: ShowTabBarOption): Promise<GeneralCallbackResult> {
    return this.toPromise<ShowTabBarOption, GeneralCallbackResult>(
      (opt) => void wx.showTabBar(opt),
      option,
    );
  }

  hideTabBar(option: HideTabBarOption): Promise<GeneralCallbackResult> {
    return this.toPromise<HideTabBarOption, GeneralCallbackResult>(
      (opt) => void wx.hideTabBar(opt),
      option,
    );
  }

  getFileInfo(option: GetFileInfoOption): Promise<GetFileInfoSuccessCallbackResult> {
    return this.toPromise<GetFileInfoOption, GetFileInfoSuccessCallbackResult>(
      (opt) => void wx.getFileSystemManager().getFileInfo(opt),
      option,
    );
  }

  getImageInfo(option: GetImageInfoOption): Promise<GetImageInfoSuccessCallbackResult> {
    return this.toPromise<GetImageInfoOption, GetImageInfoSuccessCallbackResult>(
      (opt) => void wx.getImageInfo(opt),
      option,
    );
  }
}

export const awx = new Awx();
