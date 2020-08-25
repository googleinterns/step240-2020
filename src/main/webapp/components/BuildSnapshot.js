import * as React from "react";
import {getFields} from "./utils/getFields.js";

/**
 * Component used to render Build Information.
 *
 * @param {*} props input properties containing build information
 */
export const BuildSnapshot = (props) => {
  // State variable indicating whether the BuildSnapshot is open
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
