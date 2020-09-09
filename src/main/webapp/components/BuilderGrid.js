import * as React from "react";

/**
 * Component used as an indicator for build status.
 * Different statuses are differentiated by alternative
 * style rules.
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
 *
 * @param props.status
 * @param props.name 
 */
const Builder = (props) => {
  return (
    <button className='builder'>
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
      {props.builders.map(b => <Builder status={b.status} name={b.name}/>)}
    </div>
  );
}