import * as React from "react";
import {Wrapper} from "./Wrapper";

/**
 * Component used to render Build Information.
 * @param {*} props input properties containing build information
 */
export const BuildSnapshot = (props) => {
    // State variable indicating whether the BuildSnapshot is open
    // isOpen === true indicates the tray should be visible 
    // and the indicator arrow on the Header should be facing downwards.
    const [isOpen, setIsOpen] = React.useState(false);
    const headerFields = ["description", "commitHash", "repository", "status"];
    //TODO: Determine failure group and add it to headerFields.
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

const Tray = (props) => {
  if(props.isOpen !== true) return null;
  return (
    <div>
      <Wrapper>
        <Subheading/>
        <NameTagGrid data = {{}}/>
      </Wrapper>
      <BuilderDataTable/>
    </div>
  );
}

const CommitHash = (props) => <span>{props.hash}</span>

const Description = (props) => <span>{props.description}</span>

const FailureGroup = (props) => <span>{props.group}</span>

const BuildStatus = (props) => <span>{props.status}</span>

const Subheading = (props) => <Wrapper><span>time</span></Wrapper>

const Grid = (props) => <div>{props.data.map(datapoint => <NameTag buildBotName = {datapoint.buildBotName}/>)}</div>

const NameTagGrid = (props) => <Grid data = {[]} element = {NameTag}/>

const NameTag = (props) => <span>{props.buildBotName}</span>

const BuilderDataTable = (props) => <table></table>

const getFields = (obj, fields, defaultValue) => {
  const result = { };
  for(const field of fields) result[field] = getField(obj, field, defaultValue);
  return result;
}

const getField = (obj, field, defaultValue) => obj[field] !== undefined ? obj[field] : (defaultValue !== undefined ? defaultValue : null);

const toggle = (boolean) => !boolean;