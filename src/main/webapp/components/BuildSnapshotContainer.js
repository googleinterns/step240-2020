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
            commitHash: "HASH",
            description: "Description",
            repository: "REPO LINK",
            status: "PASSED",
            builders: [],
            timestamp: "Monday"
          }}
        />
        </Wrapper>
    )
}
