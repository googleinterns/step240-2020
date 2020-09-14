/**
 * Useful for extracting a field from an object, with a fallback (defaultValue).
 * Useful where "undefined", is not a desired result for a missing field.
 *
 * @param obj the object to query the fields for.
 * @param field the target field, in string form.
 * @param defaultValue the value to be used if obj[field] === undefined.
 */
export const getField = (obj, field, defaultValue) => {
  return obj !== undefined
             ? obj[field] !== undefined
                   ? obj[field]
                   : (defaultValue !== undefined ? defaultValue : null)
             : defaultValue;
}
