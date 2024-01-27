export class Deferred<T> {
  resolve!: (value: T) => void;
  reject!: (reason?: unknown) => void;

  promise = new Promise<T>((resolve, reject) => {
    this.resolve = resolve;
    this.reject = reject;
  });
}
