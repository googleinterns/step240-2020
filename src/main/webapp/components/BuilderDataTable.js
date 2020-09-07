import * as React from 'react';

const HEADERS = ['no', 'text', 'log'];

export const BuilderDataTable = props => {
  return (
    <table className='data-table'>
      <thead>
        <tr>{HEADERS.map(header => <th>{header}</th>)}</tr>
      </thead>
      <tbody>
        {props.data.map(datapoint => 
          <tr>
            {HEADERS.forEach(header => <td>{datapoint[header]}</td>)}
          </tr>
        )}
      </tbody>
    </table>
  );
}
