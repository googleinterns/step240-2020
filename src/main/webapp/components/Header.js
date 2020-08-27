import * as React from "react";

/**
 * Main Navigation Component
 */
export const Header = () => {
  const TITLE = "LLVM Build View";

  return (
    <header>
      <Wrapper>
        <span>{TITLE}</span>
      </Wrapper>
      <Navigation/>
    </header>
  );
}
