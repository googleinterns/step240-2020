import * as React from 'react';
import {getFields} from './utils/getFields.js';

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

  const headerFields = ['description', 'commitHash', 'repository', 'status'];
  const trayFields = ['builders', 'timestamp'];

  const headerData = getFields(props.buildData, headerFields, '');
  const trayData = getFields(props.buildData, trayFields, '');

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
  * @param props an object containing a 'data' field
  * which encapsulates the fields from headerFields.
 */
const Header = (props) => {
  return (
    <div onClick = {() => props.onClick(toggle(props.isOpen))}>
      <span class = 'header-hash'>{props.data.commitHash}</span>
      <span class = 'header-description'>{props.data.description}</span>
      <FailureGroup group = {'Group'}/>
      <span class = 'header-status'>{props.data.status}</span>
    </div>
  );
}

/**
  * Subcomponent that holds the content revealed
  * when the BuildSnapshot is open (isOpen === true).
  *
  * @param props an object containing a 'data' field
  * which encapsualtes the fields from trayFields.
 */
const Tray = (props) => {
  // State variable indicating the builder whose data
  // should be rendered to the BuilderDataTable.
  // Represents the index of the builder in props.data.builders
  const [selectedBuilder,selectBuilder] = React.useState(0);
  const builder = props.data.builders[selectedBuilder];

  if(props.isOpen !== true) return null;
  return (
    <div class='tray'>
      <span class = 'tray-timespan'>{props.data.timestamp}</span>
      <div class = 'tray-currentbot'><span class = 'bot-display'>{builder.name}</span></div>
      <BuilderGrid onClick = {selectBuilder} data = {props.data.builders}/>
      <BuilderDataTable builder = {builder}/>
    </div>
  );
}
