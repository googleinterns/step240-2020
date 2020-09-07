import * as React from 'react';
import {getFields} from './utils/getFields';

// The target fields to be extracted from each build step
// and displayed on each row.
const BUILD_STEP_FIELDS = ['step_number', 'text', 'logs'];

// Headers to display at the top of the table.
const HEADERS = ['no.', 'text', 'log'];

/**
 * Table responsible for storing the data for a builder.
 *
 * @param {Object[]} props.buildSteps - The build steps for a given builder.
 * @param {string} props.buildSteps[].step_number - The relative order of the build step.
 * @param {string} props.buildSteps[].text - Output text related to the build step.
 * @param {string} props.buildSteps[].logs - A URL pointing to the log file.
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
            {BUILD_STEP_FIELDS.forEach(field => <td>{datapoint[field]}</td>)}
          </tr>
        )}
      </tbody>
    </table>
  );
}
