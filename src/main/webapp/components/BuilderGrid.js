import * as React from "react";

/**
 * Component used as an indicator for build status.
 * Statuses are differentiated by alternative
 * styling rules.
 * @param props.status the build status. Partially determines
 * the styling of the rules.
 */
const StatusIndicator = (props) => {
  const className = 'indicator #'.replace('#', props.status);
  return (
    <div className={className}></div>
  );
}

/**
 * Component used to display information for a builder.
 * @param props.status the build status of the bot.
 * @param props.name the name of the bot.
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
 * @param props.builders 
 * @param props.builders[].builder.status 
 * @param props.builders[].builder.name 
 */
export const BuilderGrid = (props) => {
  return (
    <div className='builder-grid'>
      {props.builders.map((b, idx) => <Builder key={idx} status={b.status} name={b.name} onClick={props.onClick.bind(this, idx)}/>)}
    </div>
  );
}