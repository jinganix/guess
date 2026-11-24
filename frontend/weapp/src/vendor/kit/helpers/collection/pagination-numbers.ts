// DO NOT EDIT: GENERATED FROM kit@c32c22eb4ddd729c36ecc8ee2c28362a54bf417a by tool/vendor (toolVersion 1). Host edits belong outside vendor/kit. See vendor-lock.json.
export const PAGINATION_ELLIPSIS = -1;

export interface PaginationItem {
  href: string | undefined;
  key: string;
  page: number;
  selected: boolean;
}

/** Clamp to `1..totalPage`: stale `?page=` values must render live numbers. */
export function clampPage(page: number, totalPage: number): number {
  if (!Number.isFinite(page)) {
    return 1;
  }
  return Math.min(Math.max(1, Math.floor(page)), Math.max(1, totalPage));
}

export function pageNumbers(page: number, totalPage: number): number[] {
  if (totalPage <= 7) {
    return Array.from(Array(Math.max(0, totalPage)).keys()).map((i) => i + 1);
  }
  const safePage = clampPage(page, totalPage);
  if (safePage < 3) {
    return [
      1,
      2,
      3,
      PAGINATION_ELLIPSIS,
      totalPage - 2,
      totalPage - 1,
      totalPage,
    ];
  } else if (safePage === 3) {
    return [1, 2, 3, 4, PAGINATION_ELLIPSIS, totalPage - 1, totalPage];
  } else if (safePage >= totalPage - 2) {
    return [
      1,
      2,
      PAGINATION_ELLIPSIS,
      totalPage - 3,
      totalPage - 2,
      totalPage - 1,
      totalPage,
    ];
  } else {
    return [
      1,
      PAGINATION_ELLIPSIS,
      safePage - 1,
      safePage,
      safePage + 1,
      PAGINATION_ELLIPSIS,
      totalPage,
    ];
  }
}

export function paginationData(
  page: number,
  totalPage: number,
): PaginationItem[] {
  const safePage = clampPage(page, totalPage);
  return pageNumbers(safePage, totalPage).map((number, index) => ({
    href: number === PAGINATION_ELLIPSIS ? undefined : `${number}`,
    key: `${index}`,
    page: number,
    selected: number === safePage,
  }));
}
