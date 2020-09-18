/**
 * Useful for extracting a field from an object, with a fallback (defaultValue).
 * Useful where "undefined", is not a desired result for a missing field.
 *
 * @param obj the object to query the fields for.
 * @param field the target field, in string form.
 * @param defaultValue the value to be used if obj[field] === undefined.
 * @deprecated deprecated in favour of destructuring and property shorthand.
 * @see {@link https://github.com/googleinterns/step240-2020/pull/176|GitHub}
 */
export const getField = (obj, field, defaultValue) => {
  console.warn('getField is deprecated. Use destructuring to extract values.');
  
  return obj[field] !== undefined ?
      obj[field] :
      (defaultValue !== undefined ? defaultValue : null);
}
