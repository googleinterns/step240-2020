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
    const [pageNumber, setPageNumber] = React.useState(0);
    const [data, setData] = React.useState([]);

    // Number of entries to be pulled. 20 seems reasonable enough.
    // TODO: Add options for this number on paginationNavbar
    const NUMBER = 20;

    // The starting point from which the entries are selected.
    // Relative to the starting index.
    const OFFSET = NUMBER * pageNumber;

    // The endpoint from where we get data from
    const SOURCE = `/builders/number=${NUMBER}/offset=${OFFSET}`;

    /**
     * Moves pageNumber with a set increment
     *
     * @param increment is how many pages to jump over
     *     (negative values for going back)
     */ 
    const changePage = (increment) => setPageNumber(pageNumber + increment);

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
      content = data.map(snapshot => <BuildSnapshot buildData={snapshot}/>);
    } else {
      content = <span className='loader'>
          No new revisions to display as of {new Date().toLocaleString()}.</span>;
    }

    let hasPrev = pageNumber == 0;
    let hasNext = data.length < NUMBER;

    let paginationNavbar = 
      <div className='pagination'>
        <span className={`${hasPrev ? 'hiddenButton' : 'paginationButton'} prevPage`}
              onClick={hasPrev ? null : () => changePage(-1)}></span>
        <span className={`${hasNext ? 'hiddenButton' : 'paginationButton'} nextPage`}
              onClick={hasNext ? null : () => changePage(1)}></span>
        <span className='currentPage'>Page: {pageNumber}</span>
      </div>;

    return (
      <>
        <div id='build-snapshot-container'>
          {content}
        </div>
        <div id='pagination-navbar'>
          {paginationNavbar}
        </div>
      </>
    );
  }
);
