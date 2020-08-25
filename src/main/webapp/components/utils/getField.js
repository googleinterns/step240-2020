export const getField = (obj, field, defaultValue) => obj[field] !== undefined ? obj[field] : (defaultValue !== undefined ? defaultValue : null);
