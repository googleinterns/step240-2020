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
    return (
        <Wrapper>
          <BuildSnapshot buildData = {{
            commitHash: "d8f7965e60e7b975127f7d8bc3dbd670eb93a992",
            description: "Reduce size of project cards",
            repository: "https://github.com/googleinterns/step240-2020/",
            status: "PASSED",
            builders: [],
            timestamp: "2038-01-19 03:14:07' UTC"
          }}
        />
        </Wrapper>
    )
}
