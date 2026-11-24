// DO NOT EDIT: GENERATED FROM kit@c32c22eb4ddd729c36ecc8ee2c28362a54bf417a by tool/vendor (toolVersion 1). Host edits belong outside vendor/kit. See vendor-lock.json.
export type Filter<T> = {
  key?: string;
  filter: (t: T) => boolean;
};

export type Sorter<T> = {
  key?: string;
  sort: (a: T, b: T) => number;
};

export class FilterAndSorter<T> {
  filters: Filter<T>[] = [];

  sorters: Sorter<T>[] = [];

  process(records: T[]): T[] {
    records = [...records];
    if (this.filters.length) {
      records = records.filter((x) =>
        this.filters.every((filter) => filter.filter(x)),
      );
    }
    if (this.sorters.length) {
      records.sort((a, b) => {
        for (const sorter of this.sorters) {
          const res = sorter.sort(a, b);
          if (res !== 0) {
            return res;
          }
        }
        return 0;
      });
    }
    return records;
  }

  addFilter(filter: ((t: T) => boolean) | null, key?: string): void {
    if (key) {
      if (!filter) {
        this.removeFilter(key);
      } else {
        if (this.filters.some((x) => x.key === key)) {
          this.filters = this.filters.map((x) =>
            x.key === key ? { filter, key } : x,
          );
        } else {
          this.filters = [...this.filters, { filter, key }];
        }
      }
    } else if (filter) {
      this.filters = [...this.filters, { filter }];
    }
  }

  removeFilter(key: string): void {
    this.filters = this.filters.filter((x) => x.key !== key);
  }

  addSorter(sorter: ((a: T, b: T) => number) | null, key?: string): void {
    if (key) {
      if (!sorter) {
        this.removeSorter(key);
      } else {
        if (this.sorters.some((x) => x.key === key)) {
          this.sorters = this.sorters.map((x) =>
            x.key === key ? { key, sort: sorter } : x,
          );
        } else {
          this.sorters = [...this.sorters, { key, sort: sorter }];
        }
      }
    } else if (sorter) {
      this.sorters = [...this.sorters, { sort: sorter }];
    }
  }

  removeSorter(key: string): void {
    this.sorters = this.sorters.filter((x) => x.key !== key);
  }
}
