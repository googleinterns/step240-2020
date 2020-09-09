import * as React from "react";
import {BuildSnapshotContainer} from "./components/BuildSnapshotContainer";
import {Header} from "./components/Header";

/**
 * Main App reponsible for rendering content to the page.
 */
export const App = _ => {
  return (
    <div>
      <Header/>
      <BuildSnapshotContainer/>
    </div>
  );
}
