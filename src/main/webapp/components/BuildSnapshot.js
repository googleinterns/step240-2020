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
      <span>commit hash</span>
      <span>description</span>
      <FailureGroup group = {}/>
      <BuildStatus status = {}/>
    </div>
  );
}

const FailureGroup = (props) => {
  return (
    <span>failure group</span>
  );
}

const BuildStatus = (props) => <span>{props.status}</span>

const Tray = (props) => {
  return (
    <div>
    </div>
  );
}
