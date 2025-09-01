<%@ page contentType="text/html;charset=UTF-8" pageEncoding="UTF-8" language="java" %>
<%@ page import="java.util.List,java.util.Map,java.util.HashMap,com.pahana.model.Customer,com.pahana.model.Bill" %>
<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Billing - Pahana Edu Bookshop</title>
    <link rel="stylesheet" href="<%=request.getContextPath()%>/css/variables.css">
    <link rel="stylesheet" href="<%=request.getContextPath()%>/css/bill_styles.css">
    <script>
        function printBill() { window.print(); }
        function addRow() {
            const tbody = document.querySelector('#bill-items-body');
            const tmpl = document.querySelector('#bill-item-row-template');
            const clone = tmpl.content.cloneNode(true);
            tbody.appendChild(clone);
            updateTotals();
        }
        function removeRow(btn) {
            const tr = btn.closest('tr');
            tr.parentNode.removeChild(tr);
            updateTotals();
        }
    function updateLineTotal(tr) {
            const qty = parseFloat(tr.querySelector('input[name="quantity"]').value || '0');
            const up = parseFloat(tr.querySelector('input[name="unitPrice"]').value || '0');
            tr.querySelector('.line-total').textContent = (qty * up).toFixed(2);
        }
        function updateTotals() {
            let totalUnits = 0; let totalAmount = 0;
            document.querySelectorAll('#bill-items-body tr').forEach(tr => {
                const qty = parseFloat(tr.querySelector('input[name="quantity"]').value || '0');
                const up = parseFloat(tr.querySelector('input[name="unitPrice"]').value || '0');
                totalUnits += qty;
                totalAmount += qty * up;
                tr.querySelector('.line-total').textContent = (qty * up).toFixed(2);
            });
            const tu = document.getElementById('total-units'); if (tu) tu.textContent = totalUnits;
            const ta = document.getElementById('total-amount'); if (ta) ta.textContent = totalAmount.toFixed(2);
        }
        document.addEventListener('input', (e) => {
            if (e.target && (e.target.name === 'quantity' || e.target.name === 'unitPrice')) {
                const tr = e.target.closest('tr');
                if (e.target.name === 'quantity') {
                    const sel = tr.querySelector('select[name="itemId"]');
                    const opt = sel ? sel.options[sel.selectedIndex] : null;
                    const max = opt ? parseInt(opt.getAttribute('data-stock') || '0') : 0;
                    const input = e.target;
                    let val = parseInt(input.value || '0');
                    if (max > 0) { input.max = String(max); }
                    if (!isNaN(val) && max > 0 && val > max) {
                        input.value = String(max);
                    }
                }
                updateLineTotal(tr); updateTotals();
            }
        });
        document.addEventListener('change', (e) => {
            if (e.target && e.target.name === 'itemId') {
                const opt = e.target.options[e.target.selectedIndex];
                const price = opt ? parseFloat(opt.getAttribute('data-price') || '0') : 0;
                const tr = e.target.closest('tr');
                const upInput = tr.querySelector('input[name="unitPrice"]');
                const qtyInput = tr.querySelector('input[name="quantity"]');
                if (upInput) { upInput.value = price.toFixed(2); }
                const maxStock = opt ? parseInt(opt.getAttribute('data-stock') || '0') : 0;
                if (qtyInput) {
                    if (maxStock > 0) { qtyInput.max = String(maxStock); }
                    let current = parseInt(qtyInput.value || '0');
                    if (!isNaN(current) && maxStock > 0 && current > maxStock) {
                        qtyInput.value = String(maxStock);
                    }
                }
                updateLineTotal(tr); updateTotals();
            }
        });
        document.addEventListener('DOMContentLoaded', () => {
            if (document.querySelectorAll('#bill-items-body tr').length === 0) {
                addRow();
            }
        });
    </script>
</head>

<body>
    <%@ include file="_toast.jspf" %>
    <div class="bg-decoration"></div>
    <div class="container">
        <header class="header">
            <div class="header-left">
                <div class="page-icon">🧾</div>
                <h1 class="page-title">Billing Management</h1>
            </div>
            <a href="dashboard" class="back-btn"><span>←</span> Back to Dashboard</a>
        </header>

        <div class="form-section">
            <h2 class="form-title">Generate Bill</h2>
            <% if (request.getAttribute("error") != null) { %>
                <div class="error-message"><%=request.getAttribute("error")%></div>
            <% } %>

            <%
                List<Customer> customers = (List<Customer>) request.getAttribute("customers");
            %>

            <form method="post" action="bill">
                <div class="form-grid">
                    <div class="input-group">
                        <select name="customerId" class="form-select" >
                            <option value="">Select Customer</option>
                            <% if (customers != null) { 
                                for (Customer c : customers) { %>
                                    <option value="<%=c.getId()%>" <%=request.getParameter("customerId") != null && request.getParameter("customerId").equals(String.valueOf(c.getId())) ? "selected" : "" %>>
                                        <%=c.getName()%> (ACC: <%=c.getAccountNumber()%>)
                                    </option>
                            <% } } %>
                        </select>
                    </div>
                </div>

                <div class="table-section" style="margin-top:16px;">
                    <div class="table-header" style="display:flex;justify-content:space-between;align-items:center;">
                        <h3 class="table-title">Bill Items</h3>
                        <button type="button" class="form-button" onclick="addRow()">Add Item</button>
                    </div>
                    <div class="table-wrapper">
                        <table class="data-table">
                            <thead>
                                <tr>
                                    <th>Item</th>
                                    <th>Qty</th>
                                    <th>Unit Price</th>
                                    <th>Line Total</th>
                                    <th></th>
                                </tr>
                            </thead>
                            <tbody id="bill-items-body"></tbody>
                            <tfoot>
                                <tr>
                                    <td colspan="1"></td>
                                    <td>Total Units: <span id="total-units">0</span></td>
                                    <td style="text-align:right;">Total:</td>
                                    <td>LKR <span id="total-amount">0.00</span></td>
                                    <td></td>
                                </tr>
                            </tfoot>
                        </table>
                    </div>
                </div>

                <button type="submit" class="form-button" style="margin-top:16px;">Create Bill</button>
            </form>

            <template id="bill-item-row-template">
                <tr>
                    <td>
            <select name="itemId" class="form-select">
                            <option value="">Select Item</option>
                            <% java.util.List<com.pahana.model.Item> items = (java.util.List<com.pahana.model.Item>) request.getAttribute("items");
                               if (items != null) { for (com.pahana.model.Item it : items) { 
                                   boolean outOfStock = it.getQuantity() <= 0; %>
                                <option value="<%=it.getId()%>" data-price="<%= String.format("%.2f", it.getPrice()) %>" data-stock="<%= it.getQuantity() %>" <%= outOfStock ? "disabled" : "" %>>
                                    <%=it.getName()%> <%= outOfStock ? "(out of stock)" : "(stock: " + it.getQuantity() + ")" %>
                                </option>
                            <% } } %>
                        </select>
                    </td>
            <td><input type="number" name="quantity" class="form-input" min="1" step="1" value="1" /></td>
                    <td><input type="number" name="unitPrice" class="form-input" min="0" step="0.01" value="0.00" /></td>
                    <td>LKR <span class="line-total">0.00</span></td>
                    <td><button type="button" class="action-btn delete-btn" onclick="removeRow(this)">Remove</button></td>
                </tr>
            </template>
        </div>

        <%
           Bill bill = (Bill) request.getAttribute("bill");
           if (bill != null) {
        %>
        <div class="bill-section">
            <div class="bill-container">
                <div class="bill-header receipt-header">
                    <div class="bill-logo">📚</div>
                    <div>
                        <h3 class="bill-title">Pahana Edu Bookshop</h3>
                        <p class="bill-subtitle">Official Receipt</p>
                        <p class="bill-website">www.pahanaedu.lk</p>
                    </div>
                </div>

                <div class="receipt-meta">
                    <div class="meta-row">
                        <span class="meta-label">Invoice No.</span>
                        <span class="meta-value">#<%= bill.getId() %></span>
                    </div>
                    <div class="meta-row">
                        <span class="meta-label">Date</span>
                        <span class="meta-value"><%= bill.getCreatedAt() %></span>
                    </div>
                    <div class="meta-row">
                        <span class="meta-label">Customer ID</span>
                        <span class="meta-value"><%= bill.getCustomerId() %></span>
                    </div>
                    <div class="meta-row">
                        <span class="meta-label">Customer Name</span>
                        <span class="meta-value"><%= request.getAttribute("customerName") != null ? request.getAttribute("customerName") : "N/A" %></span>
                    </div>
                </div>

                <div class="table-section receipt-items">
                    <div class="table-header"><h3 class="table-title">Items</h3></div>
                    <div class="table-wrapper">
                        <table class="data-table">
                            <thead>
                                <tr>
                                    <th style="width:48px;">#</th>
                                    <th>Item</th>
                                    <th style="text-align:right;">Qty</th>
                                    <th style="text-align:right;">Unit (LKR)</th>
                                    <th style="text-align:right;">Amount (LKR)</th>
                                </tr>
                            </thead>
                            <tbody>
                                <%
                                   java.util.List<com.pahana.model.BillItem> billItems = bill.getItems();
                                   java.util.Map<Integer, String> itemNames = new java.util.HashMap<>();
                                   java.util.List<com.pahana.model.Item> allItems = (java.util.List<com.pahana.model.Item>) request.getAttribute("items");
                                   if (allItems != null) { for (com.pahana.model.Item it : allItems) { itemNames.put(it.getId(), it.getName()); } }
                                   int idx = 1;
                                   if (billItems != null) { for (com.pahana.model.BillItem bi : billItems) { %>
                                    <tr>
                                        <td><%= idx++ %></td>
                                        <td><%= itemNames.getOrDefault(bi.getItemId(), "#"+bi.getItemId()) %></td>
                                        <td style="text-align:right;"> <%= bi.getQuantity() %> </td>
                                        <td style="text-align:right;"> <%= String.format("%.2f", bi.getUnitPrice()) %> </td>
                                        <td style="text-align:right;"> <%= String.format("%.2f", bi.getTotalPrice()) %> </td>
                                    </tr>
                                <% } } %>
                            </tbody>
                        </table>
                    </div>
                </div>

                <div class="receipt-totals">
                    <div class="total-row">
                        <span class="total-label">Total Units</span>
                        <span class="total-value"><%= bill.getTotalUnits() %></span>
                    </div>
                    <div class="total-row grand">
                        <span class="total-label">Total Amount</span>
                        <span class="total-value">LKR <%= String.format("%.2f", bill.getTotalAmount()) %></span>
                    </div>
                </div>

                <div class="receipt-footer">
                    <p>Thank you for your purchase!</p>
                </div>

                <div style="display:flex; gap:12px;">
                    <button class="print-button" onclick="printBill()" type="button">Print Receipt</button>
                    <button class="print-button" style="background:linear-gradient(135deg,#2563eb,#0ea5e9)" type="button" id="send-receipt-btn" data-bill-id="<%= bill.getId() %>">Send Receipt</button>
                </div>
            </div>
        </div>
        <%
           }
        %>

        <%
           List<Bill> bills = (List<Bill>) request.getAttribute("bills");
           if (bills != null && !bills.isEmpty()) {
        %>
        <div class="table-section">
            <div class="table-header">
                <h2 class="table-title">Billing History</h2>
                <div style="display:flex;gap:8px;align-items:center;">
                    <label style="font-size:13px;color:#444">Start:</label>
                    <input type="date" id="report-start" name="report-start" class="form-input" />
                    <label style="font-size:13px;color:#444">End:</label>
                    <input type="date" id="report-end" name="report-end" class="form-input" />
                    <a href="#" id="download-sales-report" class="form-button pdf-btn">Download Sales Report</a>
                </div>
            </div>
            <div class="table-wrapper">
                <%
                   // Build a map of customerId -> name for quick lookup
                   Map<Integer, String> customerNames = new HashMap<>();
                   List<Customer> custs = (List<Customer>) request.getAttribute("customers");
                   if (custs != null) { for (Customer c : custs) { customerNames.put(c.getId(), c.getName()); } }
                %>
                <table class="data-table">
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Customer ID</th>
                            <th>Customer Name</th>
                                <th>Created At</th>
                                <th>Total Units</th>
                            <th>Total (LKR)</th>
                            <th>Actions</th>
                        </tr>
                    </thead>
                    <tbody>
                    <%
                        com.pahana.model.User _user = (com.pahana.model.User) session.getAttribute("user");
                        boolean _isSuper = _user != null && "SUPER_ADMIN".equals(_user.getRole());
                        for (Bill b : bills) { %>
                        <tr>
                            <td><%=b.getId()%></td>
                            <td><%=b.getCustomerId()%></td>
                            <td><%= customerNames.containsKey(b.getCustomerId()) ? customerNames.get(b.getCustomerId()) : "N/A" %></td>
                            <td><%=b.getCreatedAt()%></td>
                            <td><%=b.getTotalUnits()%></td>
                            <td><%= String.format("%.2f", b.getTotalAmount()) %></td>
                            <td>
                                <div class="actions">
                                    <a href="bill?action=view&id=<%=b.getId()%>" class="action-btn view-btn">View</a>
                                    <% if (_isSuper) { %>
                                    <a href="bill?action=delete&id=<%=b.getId()%>" class="action-btn delete-btn" onclick="return confirm('Are you sure you want to delete this bill?');">Delete</a>
                                    <% } %>
                                </div>
                            </td>
                        </tr>
                    <% } %>
                    </tbody>
                </table>
            </div>
        </div>
        <% } else { %>
        <div class="table-section">
            <div class="table-header">
                <h2 class="table-title">Billing History</h2>
            </div>
            <div class="table-wrapper">
                <div class="empty-state">
                    <div class="empty-icon"></div>
                    <h3>No bills found</h3>
                    <p>Generate a bill using the form above.</p>
                </div>
            </div>
        </div>
        <% } %>
    </div>
    <script>
        const APP_CTX = '<%=request.getContextPath()%>';
        document.addEventListener('click', (e) => {
            if (e.target && e.target.id === 'send-receipt-btn') {
                const billId = e.target.getAttribute('data-bill-id');
                sendReceipt(billId);
            }
        });

        async function sendReceipt(billId) {
            try {
                const res = await fetch(APP_CTX + '/email/receipt', {
                    method: 'POST',
                    headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
                    body: 'billId=' + encodeURIComponent(billId)
                });
                if (!res.ok) {
                    const text = await res.text();
                    if (typeof showToast === 'function') {
                        showToast('Failed to send receipt: ' + text, 'error');
                    } else {
                        alert('Failed to send receipt: ' + text);
                    }
                    return;
                }
                if (typeof showToast === 'function') {
                    showToast('Receipt emailed successfully.');
                } else {
                    alert('Receipt emailed successfully.');
                }
            } catch (e) {
                if (typeof showToast === 'function') {
                    showToast('Network error while sending receipt.', 'error');
                } else {
                    alert('Network error while sending receipt.');
                }
            }
        }
    </script>
    <script>
        document.getElementById('download-sales-report').addEventListener('click', function (e) {
            e.preventDefault();
            const start = document.getElementById('report-start').value;
            const end = document.getElementById('report-end').value;
            let url = APP_CTX + '/report';
            if (start && end) {
                url += '?startDate=' + encodeURIComponent(start) + '&endDate=' + encodeURIComponent(end);
            }
            window.open(url, '_blank');
        });
    </script>

    <script src="<%=request.getContextPath()%>/js/theme.js"></script>
</body>
</html>
