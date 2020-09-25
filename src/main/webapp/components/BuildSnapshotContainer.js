// Copyright 2020 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     https://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

import * as React from "react";
import {BuildSnapshot} from "./BuildSnapshot";

/**
 * Container Component for BuildSnapshots.
 * 
 * @param {*} props input property containing an array of build data to be
 * rendered through BuildSnapshot.
 */
export const BuildSnapshotContainer = React.memo(_ =>
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

    let content;
    if (data.length > 0) {
      content = data.map((snapshot, idx) => 
          <BuildSnapshot key={idx} buildData={snapshot}/>);
    } else {
      content = <span className='loader'>
          No new revisions to display as of {new Date().toLocaleString()}.</span>;
    }

    return (
      <div id='build-snapshot-container'>
        {content}
      </div>
    );
  }
);
