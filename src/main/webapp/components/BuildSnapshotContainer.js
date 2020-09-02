import * as React from "react";
import {BuildSnapshot} from "./BuildSnapshot";
import {Wrapper} from "./Wrapper";

/**
 * Container Component for BuildSnapshots.
 * 
 * @param {*} props input property containing an array of build data to be
 * rendered through BuildSnapshot.
 */
export const BuildSnapshotContainer = (props) => {
  const SOURCE = '/data';
  const [data, setData] = React.useState([]);

  /* Fetch data when component is mounted.
   * Pass in empty array as second paramater to prevent
   * Infinite callbacks as component refreshes.
   * @see <a href="https://www.robinwieruch.de/react-hooks-fetch-data">Fetching</a>
  */
  React.useEffect(() => fetch(SOURCE).then(res => setData(res.json())), []);

  return (
    <Wrapper>
      {data.map(snapshotData => <BuildSnapshot data={snapshotData}/>)}
    </Wrapper>
  );
}
