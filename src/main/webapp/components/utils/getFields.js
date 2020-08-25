const getFields = (obj, fields, defaultValue) => {
  const result = { };
  for(const field of fields) result[field] = getField(obj, field, defaultValue);
  return result;
}
