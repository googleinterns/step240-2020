import {getField} from "./getField";

/**
 * Creates a new object whose fields are either equal to obj[field]
 * or defaultValue.
 * Useful for extracting a subset of fields from an object.
 *
 * @param obj the object to query the fields for.
 * @param fields an array of the target fields, in string form.
 * @param defaultValue the value to be used if obj[field] === undefined.
 * @deprecated extracting values with destructuring instead.
 * @see {@link https://github.com/googleinterns/step240-2020/pull/139|GitHub}
 */
export const getFields = (obj, fields, defaultValue) => {
  const res = {};
  for (const field of fields) { 
    res[field] = getField(obj, field, defaultValue);
  }
  return res;
}
