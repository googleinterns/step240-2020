import * as React from "react";
import {Header} from "./components/Header";
import {Wrapper} from "./components/Wrapper";
import {BuildSnapshotContainer} from "./components/BuildSnapshotContainer";

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
