import * as React from "react";
import {getField} from "utils/getField";

/**
  * Container element used to wrap DOM elements.
  * Used as a abstraction for "div" HTML element.
  * 
  * @param props an object specifying the child components
  * and optionally specifying a className.
  *
*/
export const Wrapper = (props) => {
  const className = getField(props,"className","");
  return (
    <div className={className}>
      {props.children}
    </div>
  );
}
