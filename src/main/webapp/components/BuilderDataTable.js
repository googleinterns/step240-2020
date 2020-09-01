import * as React from 'react';

const HEADERS = ['no.', 'text', 'log'];

export const BuilderDataTable = props => {
  return (
    <table className='data-table'>
      <thead>
        <BuilderRow 
            data={HEADERS}
            header
        />
      </thead>
      <tbody>
        {props.data.map(datapoint => <BuilderRow data = {datapoint}/>)}
      </tbody>
    </table>
  );
}
