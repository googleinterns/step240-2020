import * as React from 'react';
import {getFields} from './utils/getFields.js';

/**
 * Component used to render Build Information.
 *
 * @param {Object[]} props.buildData - Aggregated data on latest commit build.
 * @param {string} props.buildData[].description - Commit message.
 * @param {Object[]} props.buildData[].builders - Bots that built this commit.
 * @param {string} props.buildData[].commitHash - Commit hash.
 * @param {string} props.buildData[].repository - URL to the GitHub repository.
 * @param {string} props.buildData[].status - The status of the build.
 * @param {string} props.buildData[].timestamp - Timestamp of commit push.
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
    <div className='build-snapshot'>
      <Header isOpen={isOpen} onClick={setIsOpen} data={headerData}/>
      <Tray isOpen={isOpen} data={trayData}/>
    </div>
  );
}

/**
 * Subcomponent that holds the content displayed
 * when the BuildSnapshot is collapsed.
 *
 * @param {boolean} props.isOpen - Indicates if the Tray should be open.
 * @param {Object} onClick - A callback function responsible
 * for toggling the visibility of the Tray via the onClick event.
 * @param {Object} props.data - Build information to be displayed on Header.
 * @param {string} props.data[].description - The commit message.
 * @param {string} props.data[].commitHash - The commit hash.
 * @param {string} props.data[].repository - URL to the GitHub repository.
 * @param {string} props.data[].status - The status of the build.
 */
const Header = (props) => {
  return (
    <div className='snapshot-header' onClick={() => props.onClick(!props.isOpen)}>
      <span className='header-hash'>{props.data.commitHash}</span>
      <span className='header-description'>{props.data.description}</span>
      <span className='failure-group'>Group</span>
      <span className='header-status'>{props.data.status}</span>
    </div>
  );
}

/**
 * Subcomponent that holds the content revealed
 * when the BuildSnapshot is open (props.isOpen === true).
 *
 * @param {boolean} props.isOpen - Indicates if the Tray should be open.
 * @param {Object[]} props.data.builders - Bots that built this commit.
 * @param {string} props.data.builders[].name - The name of the given build bot.
 * @param {string} props.data.timestamp - The timestamp of the given build bot.
 */
const Tray = (props) => {
  // State variable indicating the builder whose data
  // should be rendered to the BuilderDataTable.
  // Represents the index of the builder in props.data.builders
  const [selectedBuilder, selectBuilder] = React.useState(0);
  const builder = props.data.builders[selectedBuilder];

  if (props.isOpen !== true) { return null };
  return (
    <div className='snapshot-tray'>
      <span className='tray-timespan'>{props.data.timestamp}</span>
      <div className='tray-currentbot'>
        <span className='bot-display'>{builder.name}</span>
      </div>
      <BuilderGrid onClick={selectBuilder} data={props.data.builders}/>
      <BuilderDataTable buildSteps={builder.buildSteps}/>
    </div>
  );
}
