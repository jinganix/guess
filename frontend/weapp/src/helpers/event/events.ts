type Callback = {
  (...args: unknown[]): void | Promise<void>;
  order?: number;
};

interface EventsMap {
  // eslint-disable-next-line @typescript-eslint/no-explicit-any
  [event: string]: any;
}

interface DefaultEvents {
  [event: string]: Callback;
}

export interface Unsubscribe {
  (): void;
}

export class Emitter<Events extends EventsMap = DefaultEvents> {
  events: Partial<{ [E in keyof Events]: Callback[] }> = {};

  on<K extends keyof Events>(event: K, cb: Events[K], order?: number): Unsubscribe {
    cb.order = order;
    this.events[event]?.push(cb) || (this.events[event] = [cb]);
    this.events[event]?.sort((a, b) => (a.order ?? 0) - (b.order ?? 0));
    return () => {
      this.events[event] = this.events[event]?.filter((x) => cb !== x);
    };
  }

  async emit<K extends keyof Events>(event: K, ...args: Parameters<Events[K]>): Promise<void> {
    const callbacks = this.events[event] || [];
    for (let i = 0, length = callbacks.length; i < length; i++) {
      await callbacks[i](...args);
    }
  }
}

export function createTinyEvents<T extends EventsMap = DefaultEvents>(): Emitter<T> {
  return new Emitter<T>();
}
