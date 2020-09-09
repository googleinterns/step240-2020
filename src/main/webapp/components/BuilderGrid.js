import * as React from "react";

const StatusIndicator = (props) => {
  const className = 'indicator #'.replace('#', props.status);
  return (
    <div className={className}></div>
  );
}

const Builder = (props) => {
  return (
    <button className='builder'>
      <StatusIndicator status={props.status}/>
      {props.name}
    </button>
  );
}

/**
 * Component Description
 *
 * @param
 */
export const BuilderGrid = (props) => {
  return (
    <div className='builderGrid'>
      {props.builders.map(b => <Builder status={b.status} name={b.name}/>)}
    </div>
  );
}