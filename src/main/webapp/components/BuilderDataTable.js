import * as React from 'react';

const HEADERS = ['no', 'text', 'log'];

/**
 * Table responsible for storing the data for a builder.
 *
 * @param {Object[]} props.buildSteps - The build steps for a given builder.
 * @param {string} props.buildSteps[].step_number - The relative order of the build step.
 * @param {string} props.buildSteps[].text - Output text related to the build step.
 * @param {string} props.buildSteps[].log - A URL pointing to the log file. 
 */
export const BuilderDataTable = props => {
  return (
    <table className='data-table'>
      <thead>
        <tr>{HEADERS.map(header => <th>{header}</th>)}</tr>
      </thead>
      <tbody>
        {props.buildSteps.map(datapoint => 
          <tr>
            {HEADERS.forEach(header => <td>{datapoint[header]}</td>)}
          </tr>
        )}
      </tbody>
    </table>
  );
}
