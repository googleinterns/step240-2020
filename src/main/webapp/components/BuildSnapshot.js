// Copyright 2020 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

import * as React from 'react';
import {BuilderDataTable} from './BuilderDataTable';
import {BuilderGrid} from './BuilderGrid';
import toggleIcon from './resources/play.svg';

/**
 * Component used to render Build Information.
 *
 * @param {Object[]} props.buildData - Aggregated data on latest commit build.
 * @param {string} props.buildData[].description - Commit message.
 * @param {Object[]} props.buildData[].builders - Bots that built this commit.
 * @param {string} props.buildData[].commitHash - Commit hash.
 * @param {string} props.buildData[].repository - URL to the GitHub repository.
 * @param {string} props.buildData[].status - The status of the build.
 *    Either 'passed', 'failed' or 'lost'.
 * @param {string} props.buildData[].timestamp - Timestamp of commit push.
 */
export const BuildSnapshot = (props) => {
  // State variable indicating whether the BuildSnapshot is open or closed.
  // isOpen === true indicates the tray should be visible 
  // and the indicator arrow on the Header should be facing downwards.
  const [isOpen, setIsOpen] = React.useState(false);

  const {builders, commitHash, description,
      repository, status, timestamp} = props.buildData;

  const headerData = {commitHash, description, repository, status};
  const trayData = {builders, timestamp};

  // Set background based on build status.
  const snapshotClassName = 'build-snapshot ' + status;

  return (
    <div className={snapshotClassName}>
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
  let headerClassName = 'snapshot-header';
  if (props.isOpen === true) {
    headerClassName = 'snapshot-header active';
  }

  return (
    <div className={headerClassName} onClick={() => props.onClick(!props.isOpen)}>
      <img className='header-toggle' src={toggleIcon}/>
      <span className='header-hash'>{props.data.commitHash}</span>
      <span className='header-description'>{props.data.description}</span>
      <span className='header-status'>{props.data.status.toUpperCase()}</span>
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

  let builderName = "";
  let buildSteps = [];
  
  if (builder !== undefined) {
    builderName = builder.name;
    buildSteps = builder.buildSteps;
  }

  const {timestamp} = props.data;
  let seconds = '';
  if (timestamp !== undefined) {
    seconds = timestamp.seconds;
  }

  if (props.isOpen !== true) { return null };
  return (
    <div className='snapshot-tray'>
      <span className='tray-timespan'>{seconds}</span>
      <div className='tray-currentbot'>
        <span className='bot-display'>{builderName}</span>
      </div>
      <BuilderGrid onClick={selectBuilder} builders={props.data.builders}/>
      <BuilderDataTable buildSteps={buildSteps}/>
    </div>
  );
}
