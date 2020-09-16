import * as React from "react";
import {BuildSnapshot} from "./BuildSnapshot";

/**
 * Container Component for BuildSnapshots.
 * 
 * @param {*} props input property containing an array of build data to be
 * rendered through BuildSnapshot.
 */
export const BuildSnapshotContainer = React.memo((props) =>
  {
    // Number of entries to be pulled.
    // TODO: Determine true value for number of entries.
    const NUMBER = 2;

    // The starting point from which the entries are selected.
    // Relative to the starting index.
    // TODO: vary multiplier based on page number.
    const OFFSET = NUMBER * 0;

    const SOURCE = `/builders/number=${NUMBER}/offset=${OFFSET}`;

    const [data, setData] = React.useState([]);

    /**
     * Fetch data when component is mounted.
     * Pass in empty array as second paramater to prevent
     * infinite callbacks as component refreshes.
     * @see <a href="www.robinwieruch.de/react-hooks-fetch-data">Fetching</a>
     */
    React.useEffect(() => {
      fetch(SOURCE)
        .then(res => res.json())
        .then(json => setData(json))
        .catch(setData([]));
    }, []);

    return (
      <div className='build-snapshot-container'>
        {data.map(snapshotData => <BuildSnapshot buildData={snapshotData}/>)}
      </div>
    );
  }
);
