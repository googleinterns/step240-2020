import * as React from "react";
import {getField} from "utils/getField";

export const Wrapper = (props) => {
  const className = getField(props,"className","");
  return (
    <div className={className}>
      {props.children}
    </div>
  );
}
