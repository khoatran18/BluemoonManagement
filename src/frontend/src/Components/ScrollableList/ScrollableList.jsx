import React, { forwardRef, useEffect, useImperativeHandle, useRef } from "react";

export const ScrollableList = forwardRef(
  ({ className = "", style = {}, children, ...rest }, forwardedRef) => {
    const innerRef = useRef(null);

    useImperativeHandle(forwardedRef, () => innerRef.current);

    useEffect(() => {
      const el = innerRef.current;
      if (!el) return;

      const onWheel = (e) => {
        // Keep scroll within this element; prevent scroll chaining to parent/window.
        e.stopPropagation();

        const deltaY = e.deltaY;
        const atTop = el.scrollTop <= 0;
        const atBottom = el.scrollTop + el.clientHeight >= el.scrollHeight - 1;

        if ((atTop && deltaY < 0) || (atBottom && deltaY > 0)) {
          e.preventDefault();
        }
      };

      // Non-passive so we can preventDefault at boundaries.
      el.addEventListener("wheel", onWheel, { passive: false });
      return () => el.removeEventListener("wheel", onWheel);
    }, []);

    return (
      <div
        ref={innerRef}
        className={className}
        style={{ overflowY: "auto", ...style }}
        {...rest}
      >
        {children}
      </div>
    );
  }
);

ScrollableList.displayName = "ScrollableList";
