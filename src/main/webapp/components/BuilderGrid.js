import * as React from "react";


const Builder = (props) => {
  return (
    <div className='builder'>
      Builder name
    <div>
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