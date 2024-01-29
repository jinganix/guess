function normalize(strArray: string[]): string {
  const resultArray = [];
  if (strArray.length === 0) {
    return "";
  }

  strArray = strArray.filter((part) => part !== "");

  if (typeof strArray[0] !== "string") {
    throw new TypeError("Url must be a string. Received " + String(strArray[0]));
  }

  if (strArray[0].match(/^[^/:]+:\/*$/) && strArray.length > 1) {
    strArray[0] = strArray.shift() + strArray[0];
  }

  if (strArray[0] === "/" && strArray.length > 1) {
    strArray[0] = strArray.shift() + strArray[0];
  }
  if (strArray[0].match(/^file:\/\/\//)) {
    strArray[0] = strArray[0].replace(/^([^/:]+):\/*/, "$1:///");
  } else {
    strArray[0] = strArray[0].replace(/^([^/:]+):\/*/, "$1://");
  }

  for (let i = 0; i < strArray.length; i++) {
    let component = strArray[i];

    if (i > 0) {
      component = component.replace(/^\/+/, "");
    }
    if (i < strArray.length - 1) {
      component = component.replace(/\/+$/, "");
    } else {
      component = component.replace(/\/+$/, "/");
    }

    if (component === "") {
      continue;
    }

    resultArray.push(component);
  }

  let str = "";

  for (let i = 0; i < resultArray.length; i++) {
    const part = resultArray[i];

    if (i === 0) {
      str += part;
      continue;
    }
    const prevPart = resultArray[i - 1];
    if ((prevPart && prevPart.endsWith("?")) || prevPart.endsWith("#")) {
      str += part;
      continue;
    }
    str += "/" + part;
  }

  str = str.replace(/\/(\?|&|#[^!])/g, "$1");

  const [beforeHash, afterHash] = str.split("#");
  const parts = beforeHash.split(/[?&]+/).filter(Boolean);
  str =
    parts.shift() +
    (parts.length > 0 ? "?" : "") +
    parts.join("&") +
    (afterHash && afterHash.length > 0 ? "#" + afterHash : "");
  return str;
}

export function urlJoin(...args: string[]): string {
  const parts = Array.from(Array.isArray(args[0]) ? args[0] : args) as string[];
  return normalize(parts);
}
