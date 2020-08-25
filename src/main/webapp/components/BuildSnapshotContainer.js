import * as React from "react";
import {Wrapper} from "./Wrapper";
import {BuildSnapshot} from "./BuildSnapshot";

/**
 * Container Component for BuildSnapshots.
 * 
 * @param {*} props input property containing an array of build data to be
 * rendered through BuildSnapshot.
 */
export const BuildSnapshotContainer = (props) => {
  const data = props.data;
  return (
    <Wrapper>
      {data.map(snapshotData => <BuildSnapshot data={snapshotData}/>)}
    </Wrapper>
  );
}
