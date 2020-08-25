/**
  * Creates a new object whose fields are either equal to obj[field] or defaultValue.
  * Useful for extracting a subset of fields from an object.
  *
  * @param obj the object to query the fields for.
  * @param fields an array of the target fields, in string form. 
  * @param defaultValue 
 */
const getFields = (obj, fields, defaultValue) => {
  const result = { };
  for(const field of fields) result[field] = getField(obj, field, defaultValue);
  return result;
}
