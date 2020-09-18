import * as React from "react";
import {getField} from "./utils/getField";

/**
 * Component used as an indicator for build status.
 * Statuses are differentiated by alternative
 * styling rules.
 * @param {string} props.status the build status. Partially determines
 * the styling of the rules.
 */
const StatusIndicator = (props) => {
  const className = 'indicator '.concat(props.status);
  return (
    <div className={className}></div>
  );
}

/**
 * Component used to display information for a builder.
 *
 * @param {string} props.status the build status of the bot.
 * @param {string} props.name the name of the bot.
 */
const Builder = (props) => {
  return (
    <button className='builder' onClick={props.onClick}>
      <StatusIndicator status={props.status}/>
      {props.name}
    </button>
  );
}

/**
 * Component used to display the list of builders that built a commit.
 *
 * @param {Object[]} props.builders the builders for the commit.
 * @param {string} props.builders[].status the build status of builder.
 * @param {string} props.builders[].name the name of the builder.
 */
export const BuilderGrid = (props) => {
  const builders = getField(props, 'builders', []);

  return (
    <div className='builder-grid'>
      {
        builders.map((b, idx) =>
            <Builder key={idx} status={b.status} name={b.name}
            onClick={props.onClick.bind(this, idx)}/>)
      }
    </div>
  );
}
