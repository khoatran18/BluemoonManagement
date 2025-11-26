import { number } from "prop-types";
import React from "react";

export const DataTableSection = () => {
  const apartments = [
    {
      id: "",
      building: "",
      room_number: "",
      apartment_owner: "",
      operation: "",
    },

  ];

 

  return (
    <div className="flex flex-col w-full items-start relative flex-1 self-stretch grow overflow-hidden">
      <div className="flex flex-col items-start relative self-stretch w-full flex-[0_0_auto] overflow-x-auto">
        <table className="w-full border-collapse">
          <thead>
            <tr className="bg-greygrey-50">
              <th className="px-6 py-3 text-left font-overline-medium font-[number:var(--overline-medium-font-weight)] text-greygrey-600 text-[length:var(--overline-medium-font-size)] tracking-[var(--overline-medium-letter-spacing)] leading-[var(--overline-medium-line-height)] [font-style:var(--overline-medium-font-style)] uppercase">
                Mã căn hộ
              </th>
              <th className="px-6 py-3 text-left font-overline-medium font-[number:var(--overline-medium-font-weight)] text-greygrey-600 text-[length:var(--overline-medium-font-size)] tracking-[var(--overline-medium-letter-spacing)] leading-[var(--overline-medium-line-height)] [font-style:var(--overline-medium-font-style)] uppercase">
                Toà
              </th>
              <th className="px-6 py-3 text-left font-overline-medium font-[number:var(--overline-medium-font-weight)] text-greygrey-600 text-[length:var(--overline-medium-font-size)] tracking-[var(--overline-medium-letter-spacing)] leading-[var(--overline-medium-line-height)] [font-style:var(--overline-medium-font-style)] uppercase">
                Số Phòng
              </th>
              <th className="px-6 py-3 text-left font-overline-medium font-[number:var(--overline-medium-font-weight)] text-greygrey-600 text-[length:var(--overline-medium-font-size)] tracking-[var(--overline-medium-letter-spacing)] leading-[var(--overline-medium-line-height)] [font-style:var(--overline-medium-font-style)] uppercase">
                Chủ Căn Hộ
              </th>
              
              <th className="px-6 py-3 text-left font-overline-medium font-[number:var(--overline-medium-font-weight)] text-greygrey-600 text-[length:var(--overline-medium-font-size)] tracking-[var(--overline-medium-letter-spacing)] leading-[var(--overline-medium-line-height)] [font-style:var(--overline-medium-font-style)] uppercase">
                Thao Tác
              </th>
            </tr>
          </thead>
          <tbody>
            {apartments.map((apartment, index) => (
              <tr
                key={apartment.id}
                className={`border-t border-greygrey-50 hover:bg-greygrey-50 transition-colors ${
                  index % 2 === 0 ? "bg-white" : "bg-[#fafbfc]"
                }`}
              >
                <td className="px-6 py-4 font-body-2-regular font-[number:var(--body-2-regular-font-weight)] text-greygrey-900 text-[length:var(--body-2-regular-font-size)] tracking-[var(--body-2-regular-letter-spacing)] leading-[var(--body-2-regular-line-height)] [font-style:var(--body-2-regular-font-style)]">
                  {apartment.id}
                </td>
                <td className="px-6 py-4 font-body-2-medium font-[number:var(--body-2-medium-font-weight)] text-greygrey-900 text-[length:var(--body-2-medium-font-size)] tracking-[var(--body-2-medium-letter-spacing)] leading-[var(--body-2-medium-line-height)] [font-style:var(--body-2-medium-font-style)]">
                  {apartment.building}
                </td>
                <td className="px-6 py-4 font-body-2-regular font-[number:var(--body-2-regular-font-weight)] text-greygrey-600 text-[length:var(--body-2-regular-font-size)] tracking-[var(--body-2-regular-letter-spacing)] leading-[var(--body-2-regular-line-height)] [font-style:var(--body-2-regular-font-style)]">
                  {apartment.room_number}
                </td>
                <td className="px-6 py-4 font-body-2-regular font-[number:var(--body-2-regular-font-weight)] text-greygrey-600 text-[length:var(--body-2-regular-font-size)] tracking-[var(--body-2-regular-letter-spacing)] leading-[var(--body-2-regular-line-height)] [font-style:var(--body-2-regular-font-style)]">
                  {apartment.apartment_owner}
                </td>
                <td className="px-6 py-4 font-body-2-regular font-[number:var(--body-2-regular-font-weight)] text-greygrey-900 text-[length:var(--body-2-regular-font-size)] tracking-[var(--body-2-regular-letter-spacing)] leading-[var(--body-2-regular-line-height)] [font-style:var(--body-2-regular-font-style)]">
                  {apartment.operation}
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      </div>
    </div>
  );
};
