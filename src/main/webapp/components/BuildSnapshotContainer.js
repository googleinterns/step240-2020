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
    const NUMBER = 2;
    const OFFSET = 3;

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
      <div>
        {data.map(snapshotData => <BuildSnapshot buildData={snapshotData}/>)}
      </div>
    );
  }
);
