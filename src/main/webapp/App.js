import * as React from "react";
import {BuildSnapshotContainer} from "./components/BuildSnapshotContainer";
import {Header} from "./components/Header";
import {Wrapper} from "./components/Wrapper";

/**
 * Main App that is Rendered to the Page.
 * 
 * @param {*} props arbituary input properties for our application to use.
 */
export const App = (props) => {
  return (
    <Wrapper>
      <Header/>
      <BuildSnapshotContainer/>
    </Wrapper>
  );
}
