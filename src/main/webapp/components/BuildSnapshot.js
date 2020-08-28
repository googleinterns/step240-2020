import * as React from "react";
import {getFields} from "./utils/getFields.js";

/**
 * Component used to render Build Information.
 *
 * @param {*} props input properties containing build information
 */
export const BuildSnapshot = (props) => {
  // State variable indicating whether the BuildSnapshot is open or closed.
  // isOpen === true indicates the tray should be visible 
  // and the indicator arrow on the Header should be facing downwards.
  const [isOpen, setIsOpen] = React.useState(false);

  const headerFields = ["description", "commitHash", "repository", "status"];
  const trayFields = ["builders", "timestamp"];

  const headerData = getFields(props.buildData, headerFields, "");
  const trayData = getFields(props.buildData, trayFields, "");

  return (
    <div>
      <Header isOpen = {isOpen} onClick = {setIsOpen} data = {headerData}/>
      <Tray isOpen = {isOpen} data = {trayData}/>
    </div>
  );
}


/**
  * Subcomponent that holds the content displayed
  * when the BuildSnapshot is collapsed.
  *
  * @param props an object containing a "data" field
  * which encapsulates the fields from headerFields.
 */
const Header = (props) => {
  return (
    <div onClick = {() => props.onClick(toggle(props.isOpen))}>
      <CommitHash hash = {props.data.commitHash}/>
      <Description description = {props.data.description}/>
      <FailureGroup group =  {"Group"}/>
      <BuildStatus status = {props.data.status}/>
    </div>
  );
}

/**
  * Subcomponent that holds the content revealed
  * when the BuildSnapshot is collapsed (isOpen === true).
  *
  * @param props an object containing a "data" field
  * which encapsualtes the fields from trayFields.
 */
const Tray = (props) => {
  if(props.isOpen !== true) return null;
  return (
    <div>
      <div>
        <Subheading time = {props.data.timestamp}/>
        <NameTagGrid data = {props.data.builders}/>
      </div>
      <BuilderDataTable/>
    </div>
  );
}
