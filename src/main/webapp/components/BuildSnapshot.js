import * as React from "react";

/**
 * Component used to render Build Information.
 * @param {*} props input properties containing build information
 */
export const BuildSnapshot = (props) => {
    // State variable indicating whether the BuildSnapshot is open
    // isOpen === true indicates the tray should be visible 
    // and the indicator arrow on the Header should be facing downwards.
    const [isOpen, setIsOpen] = useState(false);

    return (
        <div>
            <Header isOpen = {isOpen} data = { }/>
            <Tray isOpen = {isOpen} data = { }/>
        </div>
    );
}

const Header = (props) => {
  return (
    <div>
      <CommitHash hash = { }/>
      <Description description = { }/>
      <FailureGroup group =  { }/>
      <BuildStatus status = { }/>
    </div>
  );
}

const Tray = (props) => {
  return (
    <div>
      <Wrapper>
        <Subheading/>
        <Grid data = { } element = {NameTag}/>
      </Wrapper>
      <DataTable/>
    </div>
  );
}

const CommitHash = (props) => <span>{props.hash}</span>

const Description = (props) => <span>{props.description}</span>

const FailureGroup = (props) => <span>failure group</span>

const BuildStatus = (props) => <span>{props.status}</span>

const Wrapper = (props) => {
  <div>
    {props.children}
  </div>
}

const Subheading = (props) => <Wrapper><span>time</span></Wrapper>

const Grid = (props) => <div></div>

const NameTag = (props) => <span>{props.buildBotName}</span>

const DataTable = (props) => <table></table>

const getField = (obj, field, defaultValue) => obj[field] !== undefined ? field : defaultValue !== undefined ? defaultValue : null;