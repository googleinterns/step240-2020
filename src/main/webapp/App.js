import * as React from "react";
import {BuildSnapshotContainer} from "./components/BuildSnapshotContainer";
import {Header} from "./components/Header";

/**
 * Main App that is Rendered to the Page.
 * 
 * @param {*} props arbituary input properties for our application to use.
 */
export const App = (props) => {
  return (
    <div>
      <Header/>
      <BuildSnapshotContainer/>
    </div>
  );
}
