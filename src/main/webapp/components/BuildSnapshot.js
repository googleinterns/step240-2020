import * as React from "react";

/**
 * Component used to render Build Information.
 *
 * @param {*} props input properties containing build information
 */
export const BuildSnapshot = (props) => {
  const headerFields = ["description", "commitHash", "repository", "status"];
  const trayFields = ["builders", "timestamp"];

  const headerData = getFields(props.buildData, headerFields, "");
  const trayData = getFields(props.buildData, trayFields, "");

  return (
    <div>
    Snapshot
    </div>
  );
}

const getFields = (obj, fields, defaultValue) => {
  const result = { };
  for(const field of fields) result[field] = getField(obj, field, defaultValue);
  return result;
}

const getField = (obj, field, defaultValue) => obj[field] !== undefined ? obj[field] : (defaultValue !== undefined ? defaultValue : null);