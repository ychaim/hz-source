/******************************************************************************
 * Copyright © 2013-2015 The Nxt Core Developers.                             *
 *                                                                            *
 * See the AUTHORS.txt, DEVELOPER-AGREEMENT.txt and LICENSE.txt files at      *
 * the top-level directory of this distribution for the individual copyright  *
 * holder information and the developer policies on copyright and licensing.  *
 *                                                                            *
 * Unless otherwise agreed in a custom licensing agreement, no part of the    *
 * Nxt software, including this file, may be copied, modified, propagated,    *
 * or distributed except according to the terms contained in the LICENSE.txt  *
 * file.                                                                      *
 *                                                                            *
 * Removal or modification of this copyright notice is prohibited.            *
 *                                                                            *
 ******************************************************************************/

package nxt.http;

import nxt.Order;
import nxt.db.DbIterator;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.JSONStreamAware;

import javax.servlet.http.HttpServletRequest;

public final class GetAccountCurrentBidOrderIds extends APIServlet.APIRequestHandler {

    static final GetAccountCurrentBidOrderIds instance = new GetAccountCurrentBidOrderIds();

    private GetAccountCurrentBidOrderIds() {
        super(new APITag[] {APITag.ACCOUNTS, APITag.AE}, "account", "asset", "firstIndex", "lastIndex");
    }

    @Override
    JSONStreamAware processRequest(HttpServletRequest req) throws ParameterException {

        long accountId = ParameterParser.getAccount(req).getId();
        long assetId = ParameterParser.getUnsignedLong(req, "asset", false);
        int firstIndex = ParameterParser.getFirstIndex(req);
        int lastIndex = ParameterParser.getLastIndex(req);

        DbIterator<Order.Bid> bidOrders;
        if (assetId == 0) {
            bidOrders = Order.Bid.getBidOrdersByAccount(accountId, firstIndex, lastIndex);
        } else {
            bidOrders = Order.Bid.getBidOrdersByAccountAsset(accountId, assetId, firstIndex, lastIndex);
        }
        JSONArray orderIds = new JSONArray();
        try {
            while (bidOrders.hasNext()) {
                orderIds.add(Long.toUnsignedString(bidOrders.next().getId()));
            }
        } finally {
            bidOrders.close();
        }
        JSONObject response = new JSONObject();
        response.put("bidOrderIds", orderIds);
        return response;
    }

}