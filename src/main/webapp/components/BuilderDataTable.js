import * as React from 'react';

const HEADERS = ['no', 'text', 'log'];

export const BuilderDataTable = props => {
  return (
    <table>
      <thead>
        <BuilderRow 
            header={true} 
            data={HEADERS} 
        />
      </thead>
      <tbody>
        <BuilderRow data={} />
      </tbody>
    </table>
  );
}