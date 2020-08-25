import * as React from "react";
import {getFields} from "./utils/getFields.js";

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
